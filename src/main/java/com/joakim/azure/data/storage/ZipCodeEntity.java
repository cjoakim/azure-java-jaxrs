package com.joakim.azure.data.storage;

import org.apache.log4j.Logger;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Instances of this Entity class are persisted in a Storage Table; 
 * see class TableUtil.
 * 
 * @author Chris Joakim, Microsoft
 * @date   2016/06/01
 */

public class ZipCodeEntity extends TableServiceEntity {

	// Constants:
	private final static Logger logger = Logger.getLogger(ZipCodeEntity.class);
	
	// Instance variables:
	private String country;
	private String state;
	private String city;
	private Double latitude;
	private Double longitude;
	
    /**
     * Default constructor is required to avoid this exception:
     * "Class type must contain contain a nullary constructor."
     */
	public ZipCodeEntity() {
		
		super();
	}
	
	public ZipCodeEntity(String data, boolean csvData) throws IllegalArgumentException {
		
		super();
		
		if (data != null) {
			if (csvData) {
				// the data String is in CSV format and looks like this: 
				// 11455,28036,US,Davidson,NC,35.4833060000,-80.7978540000
				String[] tokens = data.split(",");
				if (tokens.length > 6) {
					String trimmed = tokens[1].trim();
					if (trimmed.length() > 2) {
						this.partitionKey = trimmed.substring(0, 2);
						this.rowKey = trimmed;
					}
					this.setCountry(tokens[2]);
					this.setCity(tokens[3]);
					this.setState(tokens[4]);
					this.setLongitude(Double.parseDouble(tokens[5]));
					this.setLatitude(Double.parseDouble(tokens[6]));
				}
				else {
					throw new IllegalArgumentException("csv line invalid; " + data);
				}
			}
			else {
				// the data String is a zip code value like this: "28036"
				String trimmed = data.trim();
				if (trimmed.length() > 2) {
					this.partitionKey = trimmed.substring(0, 2);
					this.rowKey = trimmed;
				}
			}
		}
		else {
			throw new IllegalArgumentException("zipCode invalid; " + data);
		}
    }

	public String toString() {
		
		return String.format("ZipCodeEntity %s %s -> %s %s %s %s %s",
				getPartitionKey(),
				getRowKey(),
				getCountry(),
				getState(),
				getCity(),
				getLatitude(),
				getLongitude());
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
}
