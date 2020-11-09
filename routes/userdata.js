const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.get("/",function(req,res) {
  var sql="SELECT * FROM user";
  mysqlconnection.query(sql,function (err, result,fields){
console.log(JSON.stringify(result));
  res.end(JSON.stringify(result));
  })
})
module.exports=router;
