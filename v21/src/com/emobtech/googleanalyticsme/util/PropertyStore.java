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
import java.util.Vector;

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
	 * Properties keys.
	 * </p>
	 */
	private Vector propsKeys;
	
	/**
	 * <p>
	 * Properties values.
	 * </p>
	 */
	private Vector propsValues;

	/**
	 * <p>
	 * Creates an instance of PropertyStore class.
	 * </p>
	 * @param filename Store's filename.
	 */
	public PropertyStore(String filename) {
		this.filename = filename;
		propsKeys = new Vector(5);
		propsValues = new Vector(5);
		//
		open();
	}

	/**
	 * <p>
	 * Saves the store.
	 * </p>
	 */
	public void save() {
		String key = null;
		Object value = null;
		RecordStore rsProps = null;
		//
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		DataOutputStream dos = new DataOutputStream(baos);
		//
		try {
			for (int i = propsKeys.size() -1; i >= 0; i--) {
				key = propsKeys.elementAt(i).toString();
				value = propsValues.elementAt(i);
				//
				dos.writeUTF(key);
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
		set(key, value);
	}
	
	/**
	 * <p>
	 * Puts a given int in the properties file.
	 * </p>
	 * @param key Value's key.
	 * @param value Value.
	 */
	public void putInt(String key, int value) {
		set(key, new Integer(value));
	}
	
	/**
	 * <p>
	 * Puts a given long in the properties file.
	 * </p>
	 * @param key Value's key.
	 * @param value Value.
	 */
	public void putLong(String key, long value) {
		set(key, new Long(value));
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
		return (String)get(key);
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
		final Object v = get(key);
		//
		if (v != null) {
			return ((Integer)v).intValue();
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
		final Object v = get(key);
		//
		if (v != null) {
			return ((Long)v).longValue();	
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
		return propsKeys.size();
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
						propsKeys.addElement(key);
						propsValues.addElement(dis.readUTF());
					} else if (type.equals("integer")) {
						propsKeys.addElement(key);
						propsValues.addElement(new Integer(dis.readInt()));
					} else if (type.equals("long")) {
						propsKeys.addElement(key);
						propsValues.addElement(new Long(dis.readLong()));
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

	/**
	 * <p>
	 * Set key/value.
	 * </p>
	 * @param key Key.
	 * @param value Value.
	 */
	private void set(String key, Object value) {
		final int index = propsKeys.indexOf(key);
		//
		if (index != -1) {
			propsValues.setElementAt(value, index);
		} else {
			propsKeys.addElement(key);
			propsValues.addElement(value);
		}
	}
	
	/**
	 * <p>
	 * Get key/value.
	 * </p>
	 * @param key Key.
	 * @return Value.
	 */
	private Object get(String key) {
		final int index = propsKeys.indexOf(key);
		//
		if (index != -1) {
			return propsValues.elementAt(index);
		} else {
			return null;
		}
	}
}
