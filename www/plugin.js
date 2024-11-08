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
    loadSplashAd: function () {
        exec(null, null, "AdupAd", "loadSplashAd", []);
    },
    showSplashAd: function () {
        exec(null, null, "AdupAd", "showSplashAd", []);
    },
    loadRewardAd: function () {
        exec(null, null, "AdupAd", "loadRewardAd", []);
    },
    showRewardAd: function () {
        exec(null, null, "AdupAd", "showRewardAd", []);
    },
    isSplashAdReady: function () {
        exec(null, null, "AdupAd", "isSplashAdReady", []);
    },
};

module.exports = adupAd;
