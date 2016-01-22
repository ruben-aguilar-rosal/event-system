package com.strands.interviews.eventsystem.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.strands.interviews.eventsystem.EventManager;
import com.strands.interviews.eventsystem.InterviewEvent;
import com.strands.interviews.eventsystem.InterviewEventListener;

/**
 * Manages the firing and receiving of events.
 *
 * <p>
 * Any event passed to {@link #publishEvent} will be passed through to
 * "interested" listeners.
 *
 * <p>
 * Event listeners can register to receive events via
 * {@link #registerListener(String, com.strands.interviews.eventsystem.InterviewEventListener)}
 */
public class DefaultEventManager implements EventManager {
	private final Map<String, InterviewEventListener> listeners = new HashMap<String, InterviewEventListener>();
	private final Map<Class<?>, List<InterviewEventListener>> listenersByClass = new HashMap<Class<?>, List<InterviewEventListener>>();

	public void publishEvent(final InterviewEvent event) {
		if (event == null) {
			System.err.println("Null event fired?");
			return;
		}

		sendEventTo(event, calculateListeners(event.getClass()));
	}

	private Collection<InterviewEventListener> calculateListeners(final Class<?> eventClass) {
		return listenersByClass.get(eventClass);
	}

	public void registerListener(final String listenerKey, final InterviewEventListener listener) {
		if (listenerKey == null || listenerKey.equals("")) {
			throw new IllegalArgumentException("Key for the listener must not be null: " + listenerKey);
		}

		if (listener == null) {
			throw new IllegalArgumentException("The listener must not be null: " + listener);
		}

		if (listeners.containsKey(listenerKey)) {
			unregisterListener(listenerKey);
		}

		final Class<?>[] classes = listener.getHandledEventClasses();

		for (final Class<?> classe : classes) {
			addToListenerList(classe, listener);
		}

		listeners.put(listenerKey, listener);
	}

	public void unregisterListener(final String listenerKey) {
		final InterviewEventListener listener = listeners.get(listenerKey);

		for (final List<InterviewEventListener> list : listenersByClass.values()) {
			list.remove(listener);
		}

		listeners.remove(listenerKey);
	}

	private void sendEventTo(final InterviewEvent event, final Collection<InterviewEventListener> listeners) {
		if (listeners == null || listeners.size() == 0) {
			return;
		}

		for (final Object element : listeners) {
			final InterviewEventListener eventListener = (InterviewEventListener) element;
			eventListener.handleEvent(event);
		}
	}

	private void addToListenerList(final Class<?> aClass, final InterviewEventListener listener) {
		if (!listenersByClass.containsKey(aClass)) {
			listenersByClass.put(aClass, new ArrayList<InterviewEventListener>());
		}

		listenersByClass.get(aClass).add(listener);
	}

	public Map<String, InterviewEventListener> getListeners() {
		return listeners;
	}
}
