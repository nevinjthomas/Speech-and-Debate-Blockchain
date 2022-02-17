const express = require('express')
const app = express()

const { FileSystemWallet, Gateway, Wallets } = require('fabric-network');
const path = require('path');
const fs = require('fs');  // nevin, added

//const ccpPath = path.resolve(__dirname, '..', '..', 'first-network', 'connection-org1.json'); 
const ccpPath = path.resolve(__dirname, '..', '..', 'test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');  // nevin, added
const ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));  // nevin, added

// CORS Origin
app.use(function (req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  res.setHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
  res.setHeader('Access-Control-Allow-Credentials', true);
  next();
});

app.use(express.json());

app.get('/cars', async (req, res) => {
  try {
    const walletPath = path.join(process.cwd(), 'wallet');
    //const wallet = new FileSystemWallet(walletPath);
    const wallet = await Wallets.newFileSystemWallet(walletPath);  // nevin, added
    //const userExists = await wallet.exists('user1'); 
    const userExists = await wallet.get('appUser');  // nevin, added, use appUser
    if (!userExists) {
      console.log('User does not exist in the wallet');
      res.json({status: false, error: {message: 'User not exist in the wallet'}});
      return;
    }

    const gateway = new Gateway();
    //await gateway.connect(ccpPath, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } }); 
    await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });   // nevin, added

    console.log('REST Server connected to gateway');
    const network = await gateway.getNetwork('mychannel');
    const contract = network.getContract('fabcar');
    const result = await contract.evaluateTransaction('queryAllCars');
    res.json({status: true, cars: JSON.parse(result.toString())});
  } catch (err) {
    res.json({status: false, error: err});
  }
});

app.get('/cars/:key', async (req, res) => {
  try {
    console.log('REST Server got message to query car');
    const walletPath = path.join(process.cwd(), 'wallet');
    //const wallet = new FileSystemWallet(walletPath);
    const wallet = await Wallets.newFileSystemWallet(walletPath);  // nevin, added
    //const userExists = await wallet.exists('user1');
    const userExists = await wallet.get('appUser');  // nevin, added, use appUser
    if (!userExists) {
      res.json({status: false, error: {message: 'User not exist in the wallet'}});
      return;
    }

    const gateway = new Gateway();
    //await gateway.connect(ccpPath, { wallet, identity: 'user1', discovery: { enabled: true, asLocalhost: true } });
    await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });   // nevin, added
    const network = await gateway.getNetwork('mychannel');
    const contract = network.getContract('fabcar');
    const result = await contract.evaluateTransaction('queryCar', req.params.key);
    res.json({status: true, car: JSON.parse(result.toString())});
  } catch (err) {
    console.log('REST Server error in query a car');
    res.json({status: false, error: err});
  }
});

app.post('/cars', async (req, res) => {
  if ((typeof req.body.key === 'undefined' || req.body.key === '') ||
      (typeof req.body.make === 'undefined' || req.body.make === '') ||
      (typeof req.body.model === 'undefined' || req.body.model === '') ||
      (typeof req.body.color === 'undefined' || req.body.color === '') ||
      (typeof req.body.owner === 'undefined' || req.body.owner === '')) {
    res.json({status: false, error: {message: 'Missing body.'}});
    return;
  }

  try {
    console.log('REST Server got message to create car');
    const walletPath = path.join(process.cwd(), 'wallet');
    //const wallet = new FileSystemWallet(walletPath);
    const wallet = await Wallets.newFileSystemWallet(walletPath);  // nevin, added
    //const userExists = await wallet.exists('user1');
    const userExists = await wallet.get('appUser');  // nevin, added, use appUser
    if (!userExists) {
      res.json({status: false, error: {message: 'User not exist in the wallet'}});
      return;
    }

    const gateway = new Gateway();
    //await gateway.connect(ccpPath, { wallet, identity: 'user1', discovery: { enabled: true, asLocalhost: true } });
    await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });   // nevin, added
    const network = await gateway.getNetwork('mychannel');
    const contract = network.getContract('fabcar');
    await contract.submitTransaction('createCar', req.body.key, req.body.make, req.body.model, req.body.color, req.body.owner);
    res.json({status: true, message: 'Transaction (create car) has been submitted.'})
  } catch (err) {
    console.log('REST Server error in create a car');
    res.json({status: false, error: err});
  }
});

app.put('/cars', async (req, res) => {
  if ((typeof req.body.key === 'undefined' || req.body.key === '') ||
      (typeof req.body.owner === 'undefined' || req.body.owner === '')) {
    res.json({status: false, error: {message: 'Missing body.'}});
    return;
  }

  try {
    console.log('REST Server got message to change owner');
    const walletPath = path.join(process.cwd(), 'wallet');
    //const wallet = new FileSystemWallet(walletPath);
    const wallet = await Wallets.newFileSystemWallet(walletPath);
    //const userExists = await wallet.exists('user1');
    const userExists = await wallet.get('appUser');  // nevin, added, use appUser
    if (!userExists) {
      res.json({status: false, error: {message: 'User not exist in the wallet'}});
      return;
    }

    const gateway = new Gateway();
    //await gateway.connect(ccpPath, { wallet, identity: 'user1', discovery: { enabled: true, asLocalhost: true } });
    await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });   // nevin, added
    const network = await gateway.getNetwork('mychannel');
    const contract = network.getContract('fabcar');
    await contract.submitTransaction('changeCarOwner', req.body.key, req.body.owner);
    res.json({status: true, message: 'Transaction (change car owner) has been submitted.'})
  } catch (err) {
    console.log('REST Server error in change owner');
    res.json({status: false, error: err});
  }
});

app.listen(3000, () => {
  console.log('REST Server listening on port 3000');
});
