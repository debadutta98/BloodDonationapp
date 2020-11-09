const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res) {
var sql="UPDATE user SET pass="+"'"+req.body.pass+"'"+" WHERE Id="+"'"+req.body.phone+"'"
mysqlconnection.query(sql,function (err, result,fields){
  if(err)
  {
  //  throw err;
    res.status(404).send();}
  else
  {res.status(200).send()}
})
})
module.exports=router;
