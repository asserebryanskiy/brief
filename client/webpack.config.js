const path = require('path');

module.exports = {
    entry: {
        "moderator.conference": './src/conference/moderator.conference.js',
        "player.conference": './src/conference/player.conference.js',
        "projector.conference": './src/conference/projector/projector.conference.js'
    },
    // devtool: "inline-source-map",
    // devServer: {
    //     contentBase: '../out/production/resources/static/assets/js'
    // },
    output: {
        filename: '[name]-bundle.js',
        path: path.resolve(__dirname, '../src/main/resources/static/assets/js')
    }
};