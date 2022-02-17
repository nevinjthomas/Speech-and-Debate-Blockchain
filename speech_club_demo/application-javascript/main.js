function deleteall(){
    'use strict';

    const { Gateway, Wallets } = require('fabric-network');
    const path = require('path');
    const { buildCCPOrg1, buildWallet } = require('../../test-application/javascript/AppUtil.js');

    const channelName = 'mychannel';
    const chaincodeName = 'basic';
    const mspOrg1 = 'Org1MSP';
    const walletPath = path.join(__dirname, 'wallet');
    const org1UserId = 'appUser';

    function prettyJSONString(inputString) {
        return JSON.stringify(JSON.parse(inputString), null, 2);
    }

    async function deleteAssets(ccp, wallet) {
        try {
        const gateway = new Gateway();

        console.log('Starting to connect to gateway');
        await gateway.connect(ccp,
            { wallet, identity: org1UserId, discovery: { enabled: true, asLocalhost: true } });

        const network = await gateway.getNetwork(channelName);
        const contract = network.getContract(chaincodeName);

        console.log('\n--> Submit Transaction: DeleteAllAssets, removes all assets from the ledger');
        await contract.submitTransaction('DeleteAllAssets');
        console.log('*** Result: completed');

        gateway.disconnect();
    } catch (error) {
        console.error(`******** FAILED to run the application: ${error}`);
    }
    }

    async function main() {
        try {
        const ccp = buildCCPOrg1();
        const walletPath = path.join(__dirname, 'wallet');
        const wallet = await buildWallet(Wallets, walletPath);
            await deleteAssets(ccp, wallet);
        } catch (error) {
            console.error(`******** FAILED to run the application: ${error}`);
        }
    }

    main();
}

function initledger(){
    'use strict';

const { Gateway, Wallets } = require('fabric-network');
const path = require('path');
const { buildCCPOrg1, buildWallet } = require('../../test-application/javascript/AppUtil.js');

const channelName = 'mychannel';
const chaincodeName = 'basic';
const mspOrg1 = 'Org1MSP';
const walletPath = path.join(__dirname, 'wallet');
const org1UserId = 'appUser';

function prettyJSONString(inputString) {
    return JSON.stringify(JSON.parse(inputString), null, 2);
}

async function initledger(ccp, wallet) {
    try {
	const gateway = new Gateway();

	console.log('Starting to connect to gateway');
	await gateway.connect(ccp,
	    { wallet, identity: org1UserId, discovery: { enabled: true, asLocalhost: true } });

	const network = await gateway.getNetwork(channelName);
	const contract = network.getContract(chaincodeName);

        console.log('\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger');
        await contract.submitTransaction('InitLedger');
	console.log('*** Result: completed');

	gateway.disconnect();
   } catch (error) {
	console.error(`******** FAILED to run the application: ${error}`);
   }
}

async function main() {
    try {
	const ccp = buildCCPOrg1();
	const walletPath = path.join(__dirname, 'wallet');
	const wallet = await buildWallet(Wallets, walletPath);
        await initledger(ccp, wallet);
    } catch (error) {
        console.error(`******** FAILED to run the application: ${error}`);
    }
}
alert ("ran!")

main();
}