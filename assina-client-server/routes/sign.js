var express = require('express');
var router = express.Router();
var multer = require('multer');
var axios = require('axios');

var storage = multer.diskStorage({
    destination: function (req, file, cb) {
    cb(null, 'public')
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + '-' +file.originalname )
  }
})

var upload = multer({ storage: storage }).single('file')


router.post('/', function(req, res, next) {

    const headers = {
        'Authorization': 'Bearer ' + req.fields.token
    };

    const body = {
        'credentialID': req.fields.credential,
        'numSignatures': 1,
        'hash': [
            'foobar'
        ],
        'PIN': req.fields.pin
    };

    /*const bodySign = {
        'credentialID': req.fields.credential,
        'SAD': 
    }*/

    console.log(req.fields);
    console.log(req.fields.credential);
    
    axios.post('http://localhost:8080/csc/v1/credentials/authorize', body, {
        headers: headers
    }).then(res =>{

        var bodyHash = {
            'credentialID': req.fields.credential,
            'SAD': res.data.SAD,
            'hash': [
                'foobar'
            ],
            'signAlgo': '1.2.840.113549.1.1.1'
        };

        axios.post('http://localhost:8080/csc/v1/signatures/signHash', bodyHash, {
            headers: headers
        }).then(res => console.log(res.data.signatures))
            .catch(error => console.log(error));
    }).catch(error => {
        console.log(error);
    })

    res.sendStatus(200);
});

module.exports = router;