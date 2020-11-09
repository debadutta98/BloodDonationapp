const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res) {
var sql="SELECT phone FROM request_user WHERE blood_group="+"'"+req.body.blood_group+"'";
mysqlconnection.query(sql,function (err, result,fields){
//  var s="";
  if(err)
{
  res.status(404).send("[]");
//  throw err;
}
else
{
  console.log(JSON.stringify(result));
  res.status(200).send(result);
}
})
})
module.exports=router;
