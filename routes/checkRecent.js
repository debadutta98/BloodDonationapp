const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res) {
var sql="SELECT Id,end_at,views,blood_group FROM request_user";
mysqlconnection.query(sql,function (err, result,fields){
  for(var i=0;i<result.length;i++)
  {
    if(result[i].Id==req.body.Id)
    {
    var a="["+JSON.stringify(result[i])+"]"
    res.status(200).send(a);
    console.log(a);
    }
}
res.status(404).send();
})
})
module.exports=router;
