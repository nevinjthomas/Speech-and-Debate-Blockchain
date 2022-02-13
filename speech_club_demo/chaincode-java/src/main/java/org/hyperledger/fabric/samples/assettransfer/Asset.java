/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Asset {

    @Property()
    private final String assetID;

    @Property()
    private final String firstName;

    @Property()
    private final String lastName;

    @Property()
    private final boolean active;

    @Property()
    private final String level;

    @Property()
    private final String topic;

    @Property()
    private final String state;

    // public methods
    public String getAssetID() {
        return assetID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean getActive() {
        return active;
    }

    public String getLevel() {
        return level;
    }

    public String getTopic() {
        return topic;
    }

    public String getState() {
        return state;
    }

    // constructor
    public Asset(@JsonProperty("assetID") final String assetID, @JsonProperty("firstName") final String firstName,
                 @JsonProperty("lastName") final String lastName, @JsonProperty("active") final boolean active,
                 @JsonProperty("level") final String level, @JsonProperty("topic") final String topic,
                 @JsonProperty("state") final String state) {
        this.assetID = assetID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.level = level;
        this.topic = topic;
        this.state = state;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        if (getActive() != other.getActive()) {
            return false;
        }

        return Objects.deepEquals(
                new String[] {getAssetID(), getFirstName(), getLastName(), getLevel(), getTopic(), getState()},
                new String[] {other.getAssetID(), other.getFirstName(), other.getLastName(), other.getLevel(), other.getTopic(), other.getState()});
                /*
                Objects.deepEquals(
                new int[] {getSize(), getAppraisedValue()},
                new int[] {other.getSize(), other.getAppraisedValue()});
                */
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssetID(), getFirstName(), getLastName(), getActive(), getLevel(), getTopic(), getState());
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
        + " [assetID=" + assetID + ", firstName=" + firstName + ", lastName=" + lastName + ", active=" + active
        + ", level=" + level + ", topic=" + topic + ", state=" + state + "]";
    }
}
