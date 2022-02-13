/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public final class AssetTransferTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockAssetResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> assetList;

        MockAssetResultsIterator() {
            super();

            assetList = new ArrayList<KeyValue>();

            assetList.add(new MockKeyValue("asset1",
                    "{ \"assetID\": \"asset1\", \"firstName\": \"Adam\", \"lastName\": \"Smith\", \"level\": \"intermediate\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"selection\" }"));
            assetList.add(new MockKeyValue("asset2",
                    "{ \"assetID\": \"asset2\", \"firstName\": \"John\", \"lastName\": \"Smith\", \"level\": \"beginner\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"review\" }"));
            assetList.add(new MockKeyValue("asset3",
                    "{ \"assetID\": \"asset3\", \"firstName\": \"Kelly\", \"lastName\": \"Smith\", \"level\": \"advanced\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"rehersal\" }"));
            assetList.add(new MockKeyValue("asset4",
                    "{ \"assetID\": \"asset4\", \"firstName\": \"Brad\", \"lastName\": \"Smith\", \"level\": \"advanced\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"ready\" }"));
            assetList.add(new MockKeyValue("asset5",
                    "{ \"assetID\": \"asset5\", \"firstName\": \"Jane\", \"lastName\": \"Smith\", \"level\": \"intermediate\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"selection\" }"));
            assetList.add(new MockKeyValue("asset6",
                    "{ \"assetID\": \"asset6\", \"firstName\": \"Liz\", \"lastName\": \"Smith\", \"level\": \"beginner\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"review\" }"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return assetList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        AssetTransfer contract = new AssetTransfer();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Nested
    class InvokeReadAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"assetID\": \"member1\", \"firstName\": \"Adam\", \"lastName\": \"Smith\", \"active\": \"true\", \"level\": \"intermediate\", \"topic\": \"Hard work pays off\", \"state\": \"selection\" }");

            Asset asset = contract.ReadAsset(ctx, "asset1");

            assertThat(asset).isEqualTo(new Asset("member1", "Adam", "Smith", true, "intermediate", "Hard work pays off", "selection"));
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.ReadAsset(ctx, "asset1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Test
    void invokeInitLedgerTransaction() {
        AssetTransfer contract = new AssetTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        contract.InitLedger(ctx);

        InOrder inOrder = inOrder(stub);
        inOrder.verify(stub).putStringState("asset1", "{\"active\":true,\"assetID\":\"asset1\",\"firstName\":\"adam\",\"lastName\":\"smith\",\"level\":\"intermediate\",\"state\":\"selection\",\"topic\":\"Persistance pays\"}");
        inOrder.verify(stub).putStringState("asset2", "{\"active\":true,\"assetID\":\"asset2\",\"firstName\":\"john\",\"lastName\":\"smith\",\"level\":\"beginner\",\"state\":\"review\",\"topic\":\"Benefits of hard-work\"}");
        inOrder.verify(stub).putStringState("asset3", "{\"active\":true,\"assetID\":\"asset3\",\"firstName\":\"mark\",\"lastName\":\"smith\",\"level\":\"intermediate\",\"state\":\"rehersal\",\"topic\":\"Delayed gratification\"}");
        inOrder.verify(stub).putStringState("asset4", "{\"active\":true,\"assetID\":\"asset4\",\"firstName\":\"brad\",\"lastName\":\"smith\",\"level\":\"advanced\",\"state\":\"competition ready\",\"topic\":\"Why work-hard?\"}");
        inOrder.verify(stub).putStringState("asset5", "{\"active\":true,\"assetID\":\"asset5\",\"firstName\":\"jane\",\"lastName\":\"smith\",\"level\":\"advanced\",\"state\":\"selection\",\"topic\":\"Be busy\"}");
        inOrder.verify(stub).putStringState("asset6", "{\"active\":true,\"assetID\":\"asset6\",\"firstName\":\"mary\",\"lastName\":\"smith\",\"level\":\"intermediate\",\"state\":\"review\",\"topic\":\"Sow now, reap later\"}");

    }

    @Nested
    class InvokeCreateAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"assetID\": \"asset1\", \"firstName\": \"Adam\", \"lastName\": \"Smith\", \"level\": \"intermediate\", \"topic\": \"Hard work pays off\", \"state\": \"selection\" }");

            Throwable thrown = catchThrowable(() -> {
                contract.CreateAsset(ctx, "asset1", "Adam", "Smith", "intermediate", "Hard work pays off", "selection");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 already exists");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_ALREADY_EXISTS".getBytes());
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Asset asset = contract.CreateAsset(ctx, "member1", "John", "Smith", "intermediate", "Hard work pays off", "selection");

            assertThat(asset).isEqualTo(new Asset("member1", "John", "Smith", true, "intermediate", "Hard work pays off", "selection"));
        }
    }

    @Test
    void invokeGetAllAssetsTransaction() {
        AssetTransfer contract = new AssetTransfer();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        when(stub.getStateByRange("", "")).thenReturn(new MockAssetResultsIterator());

        String assets = contract.GetAllAssets(ctx);

        assertThat(assets).isEqualTo(
        "[{\"active\":true,\"assetID\":\"asset1\",\"firstName\":\"Adam\",\"lastName\":\"Smith\",\"level\":\"intermediate\",\"state\":\"selection\",\"topic\":\"Persistant People\"},"
        + "{\"active\":true,\"assetID\":\"asset2\",\"firstName\":\"John\",\"lastName\":\"Smith\",\"level\":\"beginner\",\"state\":\"review\",\"topic\":\"Persistant People\"},"
        + "{\"active\":true,\"assetID\":\"asset3\",\"firstName\":\"Kelly\",\"lastName\":\"Smith\",\"level\":\"advanced\",\"state\":\"rehersal\",\"topic\":\"Persistant People\"},"
        + "{\"active\":true,\"assetID\":\"asset4\",\"firstName\":\"Brad\",\"lastName\":\"Smith\",\"level\":\"advanced\",\"state\":\"ready\",\"topic\":\"Persistant People\"},"
        + "{\"active\":true,\"assetID\":\"asset5\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"level\":\"intermediate\",\"state\":\"selection\",\"topic\":\"Persistant People\"},"
        + "{\"active\":true,\"assetID\":\"asset6\",\"firstName\":\"Liz\",\"lastName\":\"Smith\",\"level\":\"beginner\",\"state\":\"review\",\"topic\":\"Persistant People\"}]");
    }

    @Nested
    class TransferAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"assetID\": \"asset1\", \"firstName\": \"Adam\", \"lastName\": \"Smith\", \"level\": \"intermediate\", \"active\": \"true\", \"topic\": \"Persistant People\", \"state\": \"selection\" }");

            Asset asset = contract.TransferAssetState(ctx, "asset1", "review");

            assertThat(asset).isEqualTo(new Asset("asset1", "Adam", "Smith", true, "intermediate", "Persistant People", "review"));
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferAssetState(ctx, "asset1", "rehersal");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class UpdateAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"assetID\": \"asset1\", \"firstName\": \"Adam\", \"lastName\": \"Smith\", \"active\": \"true\", \"level\": \"intermediate\", \"topic\": \"Persistant People\", \"state\": \"selection\" }");

            Asset asset = contract.UpdateAsset(ctx, "asset1", "advanced", "Persistant People", "review", true);

            assertThat(asset).isEqualTo(new Asset("asset1", "Adam", "Smith", true, "advanced", "Persistant People", "review"));
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferAssetState(ctx, "asset1", "Alex");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class DeleteAssetTransaction {

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.DeleteAsset(ctx, "asset1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class InactivateAllAssetsTransaction {

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.InactivateAllAssets(ctx);
            });
/*
            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
*/
        }
    }

}
