package com.qnap.qdk.qtshttp.filestation;

import java.util.ArrayList;

import com.qnap.qdk.qtshttp.IQtsHttpTransferedProgressListener;
import com.qnap.qdk.qtshttp.QtsHttpCancelController;

/**
 * Interface for QTS HTTP file station API
 * @author Edison
 * 1. Supoort file station related API.
 */
public interface IQtsHttpFileStation {

	/**
	 * Get share folder list of QNAP Turbo NAS.
	 * @param cancel Cancel API.
	 * @return Share folder list.
	 * @throws Exception
	 */
	public abstract ArrayList<QtsHttpFileEntry> getShareFolderList(QtsHttpCancelController cancel) throws Exception;
	
	/**
	 * Get file list below specify path.
	 * @param path Specify folder path.
	 * @param startIndex Start index of get files.
	 * @param fileLimit Number of get files.
	 * @param cancel Cancel API.
	 * @return File list.
	 * @throws Exception
	 */
	public abstract ArrayList<QtsHttpFileEntry> getFileList(String path, int startIndex,
                                                            int fileLimit,
                                                            QtsHttpCancelController cancel) throws Exception;

	/**
	 * Upload a file to QNAP Turbo NAS.
	 * @param uploadFileFullPath Local path of upload file.
	 * @param toFilePath Destination path in QNAP Turbo NAS.
	 * @param cancel Cancel API.
	 * @param progressListener Listen progress.
	 * @throws Exception
	 */
	public abstract void uploadFileByPath(String uploadFileFullPath, String toFilePath,
                                          QtsHttpCancelController cancel,
                                          IQtsHttpTransferedProgressListener progressListener) throws Exception;
	
	/**
	 * Download a file by path to local.
	 * @param downloadFileFullPath Download file full path in QNAP Turbo NAS.
	 * @param toFilePath Destinaltion path in local.
	 * @param offset Resume download from offset.
	 * @param cancel Cancel API.
	 * @param progressListener Listen progress.
	 * @throws Exception
	 */
	public abstract void downloadFileByPath(String downloadFileFullPath, String toFilePath,
                                            long offset, QtsHttpCancelController cancel,
                                            IQtsHttpTransferedProgressListener progressListener) throws Exception;
	
	
}
