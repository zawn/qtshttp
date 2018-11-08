package com.qnap.qdk.qtshttp.filestation;

public class QtsHttpFileStationStatus {
	public static final int STATUS_SUCCESS                                  = 1;
	public static final int STATUS_FAILURE                                  = 0;
	public static final int STATUS_FAILURE_FILE_EXISTS                      = 2;
	public static final int STATUS_FAILURE_SID_INVALID                      = 3;
	public static final int STATUS_FAILURE_PERMISSION_DENIED                = 4;
	public static final int STATUS_FAILURE_FILE_NOT_EXIST                   = 5;
	public static final int STATUS_FAILURE_OPEN_FILE_FAIL                   = 7;
	public static final int STATUS_FAILURE_WFM_DISABLED                     = 8;
	public static final int STATUS_FAILURE_QUOTA_ERROR                      = 9;
	public static final int STATUS_FAILURE_ILLEGAL_NAME                     = 12;
}
