const express = require('express');
 const mysql = require('mysql');
const router=express.Router();
var con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "955696",
  database: 'blood-donation-table',
  multipleStatements:true
  });
  con.on('error', function(err) {
    if(!err)
    {
      console.log("Connected");
    }
    else {
 console.log("[mysql error]",err);
    }
});
module.exports=con;
