const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection')
router.post("/",function(req,res)
{
  var sql="SELECT phone,Url,gender FROM user"
mysqlconnection.query(sql,function (err, result,fields){
  for(var i=0;i<result.length;i++)
  {
    if(result[i].phone==req.body.phone)
{
  var a1="["+JSON.stringify(result[i])+"]";
console.log(a1);
  res.send(a1);
}
  }
})
})
module.exports=router;
