/*
 * PropertyStore.java
 * 12/12/2011
 * Google Analytics ME
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 */
package com.emobtech.googleanalyticsme.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * <p>
 * This class implements a properties file store.
 * </p>
 * @author Ernandes Mourao Junior (ernandes@gmail.com)
 * @since 2.0
 */
public final class PropertyStore {
	/**
	 * <p>
	 * Store's filename.
	 * </p>
	 */
	private String filename;
	
	/**
	 * <p>
	 * Properties hash.
	 * </p>
	 */
	private Hashtable props;
	
	/**
	 * <p>
	 * Creates an instance of PropertyStore class.
	 * </p>
	 * @param filename Store's filename.
	 */
	public PropertyStore(String filename) {
		this.filename = filename;
		props = new Hashtable(5);
		//
		open();
	}

	/**
	 * <p>
	 * Saves the store.
	 * </p>
	 */
	public void save() {
		Enumeration keys = props.keys();
		String key = null;
		Object value = null;
		RecordStore rsProps = null;
		//
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		DataOutputStream dos = new DataOutputStream(baos);
		//
		try {
			while (keys.hasMoreElements()) {
				key = keys.nextElement().toString();
				value = props.get(key);
				//
				dos.writeUTF(key.toString());
				//
				if (value instanceof String) {
					dos.writeUTF("string");
					dos.writeUTF(value.toString());
				}else if (value instanceof Integer) {
					dos.writeUTF("integer");
					dos.writeInt(((Integer)value).intValue());
				} else if (value instanceof Long) {
					dos.writeUTF("long");
					dos.writeLong(((Long)value).longValue());
				}
			}
			//
			dos.writeUTF("eof");
			rsProps = RecordStore.openRecordStore(filename, true);
			//
			byte[] data = baos.toByteArray();
			//
			if (rsProps.getNumRecords() == 0) {
				rsProps.addRecord(data, 0, data.length);
			} else {
	            RecordEnumeration re =
	            	rsProps.enumerateRecords(null, null, false);
	            //
				rsProps.setRecord(re.nextRecordId(), data, 0, data.length);
			}
		} catch (IOException e) {
		} catch (RecordStoreException e) {
		} finally {
			if (rsProps != null) {
				try {
					rsProps.closeRecordStore();
				} catch (RecordStoreException e) {
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Puts a given string in the properties file.
	 * </p>
	 * @param key Value's key.
	 * @param value Value.
	 */
	public void putString(String key, String value) {
		props.put(key, value);
	}

	/**
	 * <p>
	 * Puts a given int in the properties file.
	 * </p>
	 * @param key Value's key.
	 * @param value Value.
	 */
	public void putInt(String key, int value) {
		props.put(key, new Integer(value));
	}
	
	/**
	 * <p>
	 * Puts a given long in the properties file.
	 * </p>
	 * @param key Value's key.
	 * @param value Value.
	 */
	public void putLong(String key, long value) {
		props.put(key, new Long(value));
	}
	
	/**
	 * <p>
	 * Gets a string from the given key. If the key is not found
	 * <code>null</code> is returned.
	 * </p>
	 * @param key Value's key.
	 * @return Value.
	 */
	public String getString(String key) {
		return (String)props.get(key);
	}
	
	/**
	 * <p>
	 * Gets a int from the given key. If the key is not found
	 * <code>Integer.MIN_VALUE</code> is returned.
	 * </p>
	 * @param key Value's key.
	 * @return Value.
	 */
	public int getInt(String key) {
		if (props.contains(key)) {
			return ((Integer)props.get(key)).intValue();
		} else {
			return Integer.MIN_VALUE;
		}
	}

	/**
	 * <p>
	 * Gets a long from the given key. If the key is not found
	 * <code>Long.MIN_VALUE</code> is returned.
	 * </p>
	 * @param key Value's key.
	 * @return Value.
	 * @throws NullPointerException If the key is not found.
	 */
	public long getLong(String key) {
		if (props.contains(key)) {
			return ((Long)props.get(key)).longValue();	
		} else {
			return Long.MIN_VALUE;
		}
	}
	
	/**
	 * <p>
	 * Number of properties stored.
	 * </p>
	 * @return Number.
	 */
	public int size() {
		return props.size();
	}

	/**
	 * <p>
	 * Opens the store in order to load the properties.
	 * </p>
	 */
	void open() {
		RecordStore rsProps = null;
		//
		try {
			rsProps = RecordStore.openRecordStore(filename, true);
			//
	        if (rsProps.getNumRecords() == 1) {
	            RecordEnumeration re =
	            	rsProps.enumerateRecords(null, null, false);
	            int recordID = re.nextRecordId();
	            byte[] record = rsProps.getRecord(recordID);
	            //
	            String key = null;
	            String type = null;
	            DataInputStream dis =
	            	new DataInputStream(new ByteArrayInputStream(record));
	            //
				while (!(key = dis.readUTF()).equals("eof")) {
					type = dis.readUTF();
					//
					if (type.equals("string")) {
						props.put(key, dis.readUTF());
					} else if (type.equals("integer")) {
						props.put(key, new Integer(dis.readInt()));
					} else if (type.equals("long")) {
						props.put(key, new Long(dis.readLong()));
					}
				}
	        }
		} catch (IOException e) {
		} catch (RecordStoreException e) {
		} finally {
			if (rsProps != null) {
				try {
					rsProps.closeRecordStore();
				} catch (RecordStoreException e) {
				}
			}
		}
	}
}
