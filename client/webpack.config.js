const path = require('path');

module.exports = {
    entry: {
        "moderator.role-play": './src/roleplay/moderator.role-play.js',
        "game.role-play": './src/roleplay/game.role-play.js'
    },
    // devtool: "inline-source-map",
    devServer: {
        contentBase: '../out/production/resources/static/assets/js'
    },
    output: {
        filename: '[name]-bundle.js',
        path: path.resolve(__dirname, '../src/main/resources/static/assets/js')
    }
};