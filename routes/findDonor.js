const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res) {
console.log(req.body.blood_group);
var sql=req.body.blood_group;
console.log(sql);
mysqlconnection.query(sql,function (err, result,fields){
  if(err)
  {
    res.status(404).send();
    throw err;
  }
  else{
//res.status(200).send();
res.status(200).send(JSON.stringify(result));
}
})
})
module.exports=router;
