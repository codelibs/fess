const webpack = require("webpack");

module.exports = {
  entry: __dirname + "/src/main.js",
  output: {
    path: __dirname +'/../../../webapp/js/ss',
    filename: 'fess-ss.min.js'
  },
  plugins: [
    new webpack.optimize.UglifyJsPlugin()
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query:
        {
          presets: ['es2015','stage-0']
        }
      }
    ]
  }
};
