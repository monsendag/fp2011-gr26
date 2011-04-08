package fp.common.network;

public enum NetworkCommand {
	getActivities, getActivity, markRead, markUnread, createActivity,
	returnActivities, returnEmployees, getEmployees, getCredentials, returnCredentials, getMessages, getNewMessages, returnMessages, returnNewMessages,
	getMeetings, returnMeetings, getRooms, getAllActivities, getAllRooms, getAllMeetings, getAllMessages,
	returnAllactivities, returnAllRooms, returnAllMeetings, returnAllMessages, getAvailableRooms,
	returnAvailableRooms, getMeeting, getRoom, returnMeeting, returnRoom, addActivity,
	returnActivityID, addMeeting, returnMeetingID, cancelActivity, cancelMeeting, changeActivity,
	changeMeeting, changeInviteStatus, markMessageAsRead
	
}
