package com.yng.yngweekend.service;

import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import com.yng.yngweekend.controller.ListenerController;
import com.yng.yngweekend.domain.Listener;
import com.yng.yngweekend.domain.ListenersState;
import com.yng.yngweekend.hazelcast.ClusterMembershipListener;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ListenerService implements MembershipListener{
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerService.class);
    private final String MEMBER_LISTENERS_STATUS_MAP = "member-listeners-status";
    private final String LISTENER_STATE_TOPIC = "listener-state-update";
    private final String LISTENERS_STATE_MAP = "listeners-state";
    private final String LISTENERS_STATE_MAP_KEY = "listeners-state-key";

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    private void setupListenerManagement(){
        hazelcastInstance.getCluster().addMembershipListener(this); // Subscribe this member to add/remove cluster member events

        // To handle update listener state requests
        subscribeToListenersStateUpdateTopic(); // Subscribe to updates in LISTENER_STATE_TOPIC
        handleCurrentListenerState(); // if there is already a listenerState, adopt it, else initialize it
        // To reflect this members listener status in the MEMBER_LISTENERS_STATUS_MAP
        registerThisMembersListeners(); // Add this member and it's listeners to the distributed "members-listeners" map. This map is where cluster listener status comes from
    }


    public ListenersState updateListenersState(){
        //First, create an updated ListenerState object
        ListenersState updatedListenerState = new ListenersState("stop");//TODO: make mechanism to update ListenerState
        //Then, update the listenerState in hazelcast
        getCurrentListenerStateMap().put(LISTENER_STATE_TOPIC,updatedListenerState);
        //Then, notify cluster members of update
        hazelcastInstance.getTopic(LISTENER_STATE_TOPIC).publish(updatedListenerState);
        //And return new ListenerState
        return updatedListenerState;
    }

    public IMap<String,Map<Listener.Type,List<Listener>>> getClusterListenerStatus() {
        return hazelcastInstance.getMap(MEMBER_LISTENERS_STATUS_MAP);
    }


    public void memberAdded(MembershipEvent membershipEvent) {
        //no action needed
    }

    public void memberRemoved(MembershipEvent membershipEvent) {
        LOGGER.info("Removed: {}", membershipEvent);
        Member member = membershipEvent.getMember();
        hazelcastInstance.getMap(MEMBER_LISTENERS_STATUS_MAP).remove(member.getUuid());
    }


    private void registerThisMembersListeners() {
        // Structure to hold lists of listeners by type
        Map<Listener.Type,List<Listener>> listenerTypeToListOfListeners = new HashMap<>(); // map Listener.Type -> listOfListeners

        // Add all listener type's list to the map
        listenerTypeToListOfListeners.put(Listener.Type.KAFKA, new ArrayList<>());
        listenerTypeToListOfListeners.put(Listener.Type.MQ, new ArrayList<>());
        listenerTypeToListOfListeners.put(Listener.Type.FILE, new ArrayList<>());

        //Register member and it's listeners to the distributed "members-listeners" map
        Member member = hazelcastInstance.getCluster().getLocalMember();
        hazelcastInstance.getMap(MEMBER_LISTENERS_STATUS_MAP).put(member.getUuid(),listenerTypeToListOfListeners);
    }


    /* If there is a listenerState set in hazelcast, then adopt current listenerState, else initialize default listenerState in hazelcast*/
    private void handleCurrentListenerState() {
        // if listenerState exists
        if(getCurrentListenerStateMap().size() > 0){
            // then adopt ListenerState
            ListenersState listenersState = getCurrentListenerStateMap().get(LISTENERS_STATE_MAP_KEY);
            adoptListenersState(listenersState);
        } else {
            // else initialize ListenerState
            getCurrentListenerStateMap().put(LISTENERS_STATE_MAP_KEY,new ListenersState("default"));
        }
    }

    private void subscribeToListenersStateUpdateTopic() {
        ITopic<ListenersState> topic = hazelcastInstance.getTopic(LISTENER_STATE_TOPIC);
        topic.addMessageListener(new MessageListenerImpl());
    }

    private static void adoptListenersState(ListenersState listenersState){
        //TODO
        LOGGER.info("Adopting ListenerState...");
        //parse through ListenerState
        //update status of listeners in MEMBER_LISTENERS_STATUS_MAP
    }

    private IMap<String,ListenersState> getCurrentListenerStateMap(){
        return hazelcastInstance.getMap(LISTENERS_STATE_MAP);
    }

    private static class MessageListenerImpl implements MessageListener<ListenersState> {
        public void onMessage(Message<ListenersState> m) {
            LOGGER.info("UPDATE REQUEST RECEIVED: {}", m.getMessageObject());
            adoptListenersState(m.getMessageObject());
        }
    }
}
