package com.qnap.qdk.qtshttp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.protocol.HTTP;

public class QtsHttpUploadFileEntry extends AbstractHttpEntity implements Cloneable {
	private File mFile;
	private String mRequestHeader;
	private String mRequestTailer;
	private long mTransferedFileLengthInBytes;
	private long mTotalFileLengthInBytes;
	private boolean mCancel;
	private IQtsHttpTransferedProgressListener mProgressListener;
	
	public QtsHttpUploadFileEntry(File file, String contentType, IQtsHttpTransferedProgressListener progressListener) {
		super();
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		this.mFile = file;
		setContentType(contentType);
		mRequestHeader = twoHyphens
				+ boundary
				+ lineEnd
				+ "Content-Disposition: form-data;name=\"uploadfile\";filename=\""
				+ this.mFile.getName()
				+ "\"" + lineEnd
				+ "Content-Type: application/x-www-form-urlencoded"
				+ lineEnd + lineEnd;

		mRequestTailer = lineEnd + twoHyphens + boundary + twoHyphens + lineEnd;
		mTransferedFileLengthInBytes= 0;
		mTotalFileLengthInBytes = mFile.length();
		mCancel = false;
		mProgressListener = progressListener;
	}
	
	@Override
	public InputStream getContent() throws IOException,
	IllegalStateException {
		// TODO Auto-generated method stub
		return new FileInputStream(this.mFile);
	}

	@Override
	public long getContentLength() {
		// TODO Auto-generated method stub
		try {
			byte [] headerByteArray = mRequestHeader.getBytes(HTTP.UTF_8);
			int requestHeaderLength = headerByteArray.length;
			int requestTailerLength = mRequestTailer.length();

			return (requestHeaderLength + this.mFile.length() + requestTailerLength);
		} catch (Exception e) {
		}

		return -1;
	}

	@Override
	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void writeTo(OutputStream outStream) throws IOException {
		// TODO Auto-generated method stub
		if (outStream == null)
		{
			throw new IllegalArgumentException("Output stream may not be null");
		}
		InputStream fileInputStream = new FileInputStream(this.mFile);
		try
		{
			int bytesRead = -1;
			int inputcount = 1;
			int maxBufferSize = 8192; // 8 * 1024
			byte[] buffer = new byte[maxBufferSize];
			outStream.write(mRequestHeader.getBytes(HTTP.UTF_8));
			while (!mCancel && ((bytesRead = fileInputStream.read(buffer)) != -1) && mTransferedFileLengthInBytes < mTotalFileLengthInBytes ) {
				++inputcount;
				if (inputcount % 100 == 0) {
					System.gc();
				}

				outStream.write(buffer, 0, bytesRead);
				mTransferedFileLengthInBytes = mTransferedFileLengthInBytes
						+ bytesRead;
				
				mProgressListener.onProgress(mTransferedFileLengthInBytes);
			}
			outStream.write(mRequestTailer.getBytes());
			outStream.flush();
		}
		finally
		{
			outStream.close();
			fileInputStream.close();
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
