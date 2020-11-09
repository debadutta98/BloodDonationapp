const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res){
  var sql="UPDATE request_user SET views="+"'"+req.body.views+"'"+" WHERE Id="+"'"+req.body.Id+"'";
  console.log(req.body.Id+"hello");
  mysqlconnection.query(sql,function (err, result,fields){
    if(err)
    {
      //throw err;
      res.status(404).send();}
    else
    {res.status(200).send()}
  })
})
module.exports=router;
