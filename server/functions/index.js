'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotifications = functions.database.ref('/Notifications/{pushId}')
    .onWrite(event => {
        const message = event.data.current.val();
        const senderUid = message.from;
        const receiverUid = message.to;
        const promises = [];

        if (senderUid == receiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref('/Users/'+ receiverUid +'/tokenId').once('value');
        const getSenderUidPromise = admin.database().ref('/Users/'+senderUid).once('value');

        return Promise.all([getInstanceIdPromise, getSenderUidPromise]).then(results => {
            const instanceId = results[0].val();

            const sender = results[1].val();
            console.log('notifying ' + receiverUid + ' about ' + message.body + ' from ' + senderUid);
            console.log(instanceId);
            console.log('sendName:'+ sender.name + ' body:'+message.body + ' iconUrl:'+sender.profileIcon );
            const payload = {
                notification: {
                    title: sender.name,
                    body: message.body,
                    icon: sender.profileIcon
                }
            };

            admin.messaging().sendToDevice(instanceId, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);

                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
