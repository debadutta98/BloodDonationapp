const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res) {
  var obj=[req.body.Id,req.body.phone,req.body.views,req.body.blood_group,req.body.start_at,req.body.end_at];
  var sql="INSERT INTO request_user (Id,phone,views,blood_group,start_at,end_at) VALUES (?,?,?,?,?,?)";
console.log(obj);;
  mysqlconnection.query(sql,obj,function (err, result) {
    if (err)
    {
      throw err;
      res.status(404).send();
       //throw err;
    }
    else{
      console.log("1 record inserted");
      res.status(200).send();
    }
  });
})
module.exports=router;
