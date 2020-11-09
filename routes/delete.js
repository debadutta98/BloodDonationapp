const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res){
var sql="DELETE FROM request_user WHERE Id"+"="+"'"+req.body.Id+"'";
  mysqlconnection.query(sql,function (err, result) {
    if(err)
    {
      res.status(404).send();
    }
    else{
      console.log("1 record deleted");
      res.status(200).send();
    }
  });
})
module.exports=router;
