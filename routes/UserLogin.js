const express = require('express');
const router=express.Router();
const bodyparser = require('body-parser');
router.use(bodyparser.json());
const mysqlconnection = require('../connection');
router.post("/",function(req,res) {
  var sql="SELECT phone, pass FROM user"
  mysqlconnection.query(sql,function (err, result,fields) {
    if (err) throw err;
    for(var i=0;i<result.length;i++)
    {
      if(result[i].phone==req.body.phone)
      {
        if(result[i].pass==req.body.pass)
        {
          res.status(200).send();
        }
      }
    console.log(result[i].phone,result[i].pass);
    }
      res.status(404).send();
  });
})
module.exports=router;
