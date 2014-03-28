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

public class Event {

	private EventbusTopic topic = null;
	private Integer targetObjectId = null;
	private Class<? extends TopicSubscriber<Object>> targetClass = null;
	
	public Event(EventbusTopic topic) {
		super();
		this.topic = topic;
	}
	
	public Event(EventbusTopic topic, Integer targetObjectId) {
		super();
		this.topic = topic;
		this.targetObjectId = targetObjectId;
	}
	
	public Event(EventbusTopic topic, Class<? extends TopicSubscriber<Object>> targetClass) {
		super();
		this.topic = topic;
		this.targetClass = targetClass;
	}

	public EventbusTopic getTopic() {
		return topic;
	}

	public void setTopic(EventbusTopic topic) {
		this.topic = topic;
	}

	public Integer getTargetObjectId() {
		return targetObjectId;
	}

	public void setTargetObjectId(Integer targetObjectId) {
		this.targetObjectId = targetObjectId;
	}

	public Class<? extends TopicSubscriber<Object>> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<? extends TopicSubscriber<Object>> targetClass) {
		this.targetClass = targetClass; 
	}
	
	
	
	
}
