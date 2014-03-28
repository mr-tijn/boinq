/*  
 *   Copyright 2012-2014 Martijn Devisscher
 *
 *   This file is part of boinq.
 *
 *   boinq is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   boinq is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with boinq.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genohm.viewsGWT.client.eventbus;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.util.SC;

public class EventBus {

	static Map<EventbusTopic, List<Subscription>> topicSubscribersMap = new HashMap<EventbusTopic, List<Subscription>>();
	
	public static void publish(Event event, Object eventObject) {
		if (event != null) {
			publish(event.getTopic(), event.getTargetObjectId(), event.getTargetClass(), eventObject);
		}
	}
	
	public static void publish(EventbusTopic topic, Object eventObject) {
		publish(topic, null, null, eventObject);
	}
	
	
	
	/**
	 * Publish an Object to a topic if the topic exists
	 * 
	 * @param topic
	 *          the topic being published to
	 * @param o
	 *          the event object being published
	 */
	@SuppressWarnings("unchecked")
	private static void publish(EventbusTopic topic, Integer targetId, Class<? extends TopicSubscriber<Object>> targetClass, Object eventObject) {
		List<Subscription> topicSubscribers = topicSubscribersMap.get(topic);
		if (topicSubscribers != null) {
			ArrayList<Subscription> tmpList = new ArrayList<Subscription>();
			for (Subscription subscription : topicSubscribers) {
				tmpList.add(subscription);
			}
			try { // do not propagate errors that occurred at the receptors back to
						// senders
				for (Subscription subscriber : tmpList) {
					@SuppressWarnings("rawtypes")
					TopicSubscriber listener = subscriber.getListener();
					if ( (targetId != null && listener.getId() != targetId) || 
						 (targetClass != null && !listener.getClass().getName().equals(targetClass.getName()))) {
						//logger.log(Level.INFO , "*** EventBus NOT publishing " + subscriber.getTopic() + " to " + listener.getClass().getName());
					} else {
						listener.onEvent(subscriber, eventObject);
						SC.logDebug("*** EventBus publishing " + subscriber.getTopic() + " to " + listener.getClass().getName());										
					} 
				}
			} catch (Exception e) {
				//logger.log(Level.WARNING, "", e);
				String stackTrace = "";
				for (StackTraceElement el: e.getStackTrace()) stackTrace += el;
				SC.logWarn("*** EventBus caucht exception : "+stackTrace);
			}
		}
	}

	/**
	 * Creates a subscription to a given topic, if the topic doesen't exists a new
	 * one will be created
	 * 
	 * @param topic
	 *          the topic for the subscription
	 * @param listener
	 *          the listener to notify when receiving messages
	 * @return a {@link Subscription} that represents this subscription, to be
	 *         used later by {@link #unsubscribe(Subscription)}
	 */
	public static Subscription subscribe(EventbusTopic topic,
			TopicSubscriber<?> listener) {
		Subscription subscription = new Subscription(topic, listener);
		List<Subscription> topicSubscribers = topicSubscribersMap.get(topic);
		if (topicSubscribers == null) {
			topicSubscribers = new ArrayList<Subscription>();
			topicSubscribersMap.put(topic, topicSubscribers);
		}
		topicSubscribers.add(subscription);
		return subscription;
	}

	/**
	 * Removes a subscription to the topic indicated by it if topic still exists
	 * (if this subscription is the last one in the topic, the topic itself will
	 * be removed from the {@link EventBus})
	 * 
	 * @param subscription
	 *          the subscription to be removed
	 */
	public static void unsubscribe(Subscription subscription) {
		EventbusTopic topic = subscription.getTopic();
		List<Subscription> topicSubscribers = topicSubscribersMap.get(topic);
		if (topicSubscribers != null) {
			topicSubscribers.remove(subscription);
			if (topicSubscribers.isEmpty()) {
				topicSubscribersMap.remove(topic);
			}
		}
	}
}
