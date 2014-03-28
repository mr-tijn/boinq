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


/**
 * Class that represents a subscription to a given topic with the given listener
 * 
 * @author mihai.ile@gmail.com
 * 
 */
public class Subscription {

	private final EventbusTopic topic;
	private final TopicSubscriber<?> listener;

	/**
	 * Creates a new subscription reference given the topic and it's listener
	 * 
	 * @param topic
	 *            the topic for the subscription
	 * @param listener
	 *            the listener associated to it
	 */
	protected Subscription(EventbusTopic topic, TopicSubscriber<?> listener) {
		this.topic = topic;
		this.listener = listener;
	}

	/**
	 * Returns the topic for the subscription
	 * 
	 * @return a {@link String} containing the topic for the subscription
	 */
	public EventbusTopic getTopic() {
		return topic;
	}

	/**
	 * Returns the listener associated with the subscription
	 * 
	 * @return a {@link TopicSubscriber} containing the listener for the
	 *         subscription
	 */
	public TopicSubscriber<?> getListener() {
		return listener;
	}

}
