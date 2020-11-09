
const express = require('express');
 const mysql = require('mysql');
const app = express();
const bodyparser = require('body-parser');
app.use(bodyparser.json());
const connection = require('./connection');
var things = require('./routes/users');
var login=require('./routes/UserLogin');
var check=require('./routes/checkuser');
var sendtable=require('./routes/userdata');
var newtab=require('./routes/request');
var newrecent=require('./routes/checkRecent');
var update_user=require('./routes/update');
var present=require('./routes/present');
var deleterecord=require('./routes/delete');
var findDonor=require('./routes/findDonor');
var findRequire=require('./routes/findRequire');
var update=require('./routes/updateuser');
app.use('/forgetpassword',update);
app.use('/findDonor',findDonor);
app.use('/findRequire',findRequire);
app.use('/deleteuser',deleterecord);
app.use('/present',present);
app.use('/update',update_user);
app.use('/checkuser',newrecent);
app.use('/newrequest',newtab);
app.use('/data',sendtable);
app.use("/profile",check);
app.use("/login",login);
app.use("/users",things);

app.get("/",function(request,response){
  response.send("hello");
})

app.listen("3000",function(){
  console.log("server start responding");
});
