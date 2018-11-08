package com.qnap.qdk.qtshttp;

/**
 * Interface of transfered progress listener
 */
public interface IQtsHttpTransferedProgressListener {
	public abstract void onProgress(long transferedFileLengthInBytes);
}
