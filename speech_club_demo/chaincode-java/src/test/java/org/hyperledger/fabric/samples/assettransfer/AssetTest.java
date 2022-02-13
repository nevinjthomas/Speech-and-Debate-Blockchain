/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class AssetTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Asset asset = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");

            assertThat(asset).isEqualTo(asset);
        }

        @Test
        public void isSymmetric() {
            Asset assetA = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");
            Asset assetB = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");

            assertThat(assetA).isEqualTo(assetB);
            assertThat(assetB).isEqualTo(assetA);
        }

        @Test
        public void isTransitive() {
            Asset assetA = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");
            Asset assetB = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");
            Asset assetC = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");

            assertThat(assetA).isEqualTo(assetB);
            assertThat(assetB).isEqualTo(assetC);
            assertThat(assetA).isEqualTo(assetC);
        }

        @Test
        public void handlesInequality() {
            Asset assetA = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");
            Asset assetB = new Asset("member2", "Jane", "Smith", false, "intermediate", "Don't be Lazy", "selection");

            assertThat(assetA).isNotEqualTo(assetB);
        }

        @Test
        public void handlesOtherObjects() {
            Asset assetA = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");
            String assetB = "not a asset";

            assertThat(assetA).isNotEqualTo(assetB);
        }

        @Test
        public void handlesNull() {
            Asset asset = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");

            assertThat(asset).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesAsset() {
        Asset asset = new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection");
        System.out.println(asset.toString());
        assertThat(asset.toString()).isEqualTo("Asset@5e07dac [assetID=member1, firstName=Adam, lastName=Smith, active=true, level=intermediate, topic=Hard work pays off, state=selection]");
    }
}
