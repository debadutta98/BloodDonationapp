
const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res){
  var obj=[
req.body.Id,
req.body.username,
req.body.gender,
req.body.address,
req.body.blood_group,
req.body.phone,
req.body.pass,
req.body.Url
]
  console.log(obj)

  var sql = "INSERT INTO user (ID,username,gender,address,blood_group,phone,pass,Url) VALUES (?,?,?,?,?,?,?,?)";
   mysqlconnection.query(sql,obj,function (err, result) {
     if (err)
     {
       res.status(404).send();
        
     }
     else{
       console.log("1 record inserted");
       res.status(200).send();
     }
   });
})

module.exports = router;
