const path = require('path');

module.exports = {
    entry: {
        "moderator.role-play": './src/moderator.role-play.js',
        "game.role-play": './src/game.role-play.js'
    },
    devtool: "inline-source-map",
    devServer: {
        contentBase: '../out/production/resources/static/assets/js'
    },
    output: {
        filename: '[name]-bundle.js',
        path: path.resolve(__dirname, '../src/main/resources/static/assets/js')
    }
};