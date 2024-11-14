var exec = require("cordova/exec");

var PLUGIN_NAME = "AdupAd";

var adupAd = {
    initAdup: function (
        appId,
        rewardAdPlacementId
    ) {
        exec(null, null, "AdupAd", "initAdup", [
            appId,
            rewardAdPlacementId,
        ]);
    },
    requestPermission: function () {
        exec(null, null, "AdupAd", "requestPermission", []);
    },
    showRewardAd: function () {
        exec(null, null, "AdupAd", "showRewardAd", []);
    }
};

module.exports = adupAd;
