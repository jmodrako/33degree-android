package io.scalac.degree.items;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

public class TalkItem {
	
	private int			id;
	private String		topic;
	private String		type;
	private String		description;
	private int			roomID;
	private int			speakerID;
	private Integer	speaker2ID	= null;
	private int			timeslotID;
	private Date		startTime;
	private Date		endTime;
	private Date		dateTime;
	
	public static void fillList(ArrayList<TalkItem> talkItemsList,
			JSONArray jsonArray,
			ArrayList<TimeslotItem> timeslotItemsList) {
		talkItemsList.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					TalkItem talkItem = new TalkItem(jsonObject, timeslotItemsList);
					talkItemsList.add(talkItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void fillList(ArrayList<TalkItem> talkItemsList,
			JSONArray jsonArray,
			ArrayList<TimeslotItem> timeslotItemsList,
			int roomID) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					TalkItem talkItem = new TalkItem(jsonObject, timeslotItemsList);
					if (talkItem.getRoomID() == roomID)
						talkItemsList.add(talkItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static TalkItem getByID(int id, ArrayList<TalkItem> talkItemsList) {
		for (TalkItem talkItem : talkItemsList) {
			if (talkItem.getId() == id)
				return talkItem;
		}
		return null;
	}
	
	public static ArrayList<TalkItem> getRoomTalkList(ArrayList<TalkItem> talkItemsList, int roomID, long dateMS) {
		ArrayList<TalkItem> roomTalkItemsList = new ArrayList<TalkItem>();
		for (TalkItem talkItem : talkItemsList) {
			if (talkItem.getRoomID() == roomID && talkItem.getDateTime().getTime() == dateMS)
				roomTalkItemsList.add(talkItem);
		}
		return roomTalkItemsList;
	}
	
	public static ArrayList<TalkItem> getTimeslotTalkList(ArrayList<TalkItem> talkItemsList, int timeslotID) {
		ArrayList<TalkItem> roomTalkItemsList = new ArrayList<TalkItem>();
		for (TalkItem talkItem : talkItemsList) {
			if (talkItem.getTimeslotID() == timeslotID)
				roomTalkItemsList.add(talkItem);
		}
		return roomTalkItemsList;
	}
	
	public static ArrayList<TalkItem>
			getNotificationTalkList(ArrayList<TalkItem> talkItemsList, Map<String, ?> notifyMap) {
		ArrayList<TalkItem> roomTalkItemsList = new ArrayList<TalkItem>();
		for (TalkItem talkItem : talkItemsList) {
			if (notifyMap.containsKey(String.valueOf(talkItem.getId())))
				roomTalkItemsList.add(talkItem);
		}
		return roomTalkItemsList;
	}
	
	public TalkItem(JSONObject jsonObject, ArrayList<TimeslotItem> timeslotItemsList) {
		this.id = jsonObject.optInt("id");
		this.topic = jsonObject.optString("topic");
		this.description = jsonObject.optString("description");
		try {
			this.type = jsonObject.getString("type");
		} catch (JSONException e1) {
			this.type = "Talk";
			// e1.printStackTrace();
		}
		try {
			this.roomID = jsonObject.getJSONObject("room").optInt("id");
		} catch (JSONException e1) {
			// e1.printStackTrace();
		}
		try {
			this.speakerID = jsonObject.getJSONObject("speaker").optInt("id");
		} catch (JSONException e1) {
			// e1.printStackTrace();
		}
		try {
			this.speaker2ID = jsonObject.getJSONObject("speaker2").optInt("id");
		} catch (JSONException e1) {
			this.speaker2ID = null;
			// e1.printStackTrace();
		}
		try {
			this.timeslotID = jsonObject.getJSONObject("timeslot").optInt("id");
			TimeslotItem timeslotItem = TimeslotItem.getByID(timeslotID, timeslotItemsList);
			this.startTime = timeslotItem.getStartTime();
			this.endTime = timeslotItem.getEndTime();
			this.dateTime = timeslotItem.getDateTime();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static class TopicComparator implements Comparator<TalkItem> {
		
		private Collator	c	= Collator.getInstance(Locale.getDefault());
		
		@Override
		public final int compare(TalkItem lhs, TalkItem rhs) {
			return c.compare(lhs.getTopic(), rhs.getTopic());
		}
		
	}
	
	public static class TimeComparator implements Comparator<TalkItem> {
		
		@Override
		public final int compare(TalkItem lhs, TalkItem rhs) {
			return Long.valueOf(lhs.getStartTime().getTime()).compareTo(Long.valueOf(rhs.getStartTime().getTime()));
		}
		
	}
	
	public int getId() {
		return id;
	}
	
	public String getTopic() {
		if (getType().equalsIgnoreCase("Talk"))
			return topic;
		else
			return getType() + ": " + topic;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getType() {
		return type;
	}
	
	public CharSequence getTopicHtml() {
		return Html.fromHtml(getTopic());
	}
	
	public CharSequence getDescriptionHtml() {
		return Html.fromHtml(getDescription());
	}
	
	public int getRoomID() {
		return roomID;
	}
	
	public int getSpeakerID() {
		return speakerID;
	}
	
	public boolean hasSpeaker2() {
		return speaker2ID != null;
	}
	
	public Integer getSpeaker2ID() {
		return speaker2ID;
	}
	
	public int getTimeslotID() {
		return timeslotID;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public Date getDateTime() {
		return dateTime;
	}
}
