/* eslint-disable import/no-commonjs */
/* eslint-disable global-require */
/* eslint-disable import/no-nodejs-modules */

const path = require('path');
const webpack = require('webpack');
const historyApiFallback = require("connect-history-api-fallback");
const webpackDevMiddleware = require('webpack-dev-middleware');
const webpackHotMiddleware = require('webpack-hot-middleware');
const config = require('./webpack.config');

const express = require('express');
const http = require('http');
const httpProxy = require('http-proxy');
const forwardHost = 'localhost';
const forwardPort = 8080;

const app = express();
const server = http.createServer(app);

const PORT = 3000;

const compiler = webpack(config);

app.use(historyApiFallback());
app.use(webpackDevMiddleware(compiler, {noInfo: true, publicPath: config.output.publicPath}));
app.use(webpackHotMiddleware(compiler));

const root = path.join(__dirname, '/src');

app.use('/static', express.static(root));

const apiProxy = httpProxy.createProxyServer({
    target: {
        host: forwardHost,
        port: forwardPort
    }
});

apiProxy.on('error', function (err, req, res) {
    console.warn('API proxy error: ' + err);
    res.end('Error.');
});

console.info(`Forwarding API requests to http://${forwardHost}:${forwardPort}`);

app.all('/api/*', (req, res) => {
    apiProxy.web(req, res);
});

app.get('*', function(req, res) {
    res.sendFile(path.join(__dirname, 'src/index.html'));
});

server.on('upgrade', (req, socket, head) => {
    apiProxy.ws(req, socket, head);
});

server.listen(PORT, '0.0.0.0', (error) => {
    if (error) {
        console.error(error);
    } else {
        console.info(`==> 🌎  Listening on port ${PORT}. Open up http://localhost:${PORT}/ in your browser.`);
    }
});
