const express = require('express');
const pjson = require('./package.json');
const app = express();

const port = 8090;

app.use(express.static(`./dist/${pjson.name}`));

// Route all through index.html
app.get('/*', (req, res) =>
    res.sendFile('index.html', {root: `dist/${pjson.name}`}),
);

console.log(`${pjson.name} running on port ${port}`)

app.listen(process.env.PORT || port);