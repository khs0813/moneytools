import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const DIST_DIR = path.resolve(__dirname, '../dist');
const PORT = process.env.PORT || 3000;

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.mjs': 'text/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.xml': 'application/xml; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.ico': 'image/x-icon',
  '.txt': 'text/plain; charset=utf-8',
  '.webmanifest': 'application/manifest+json'
};

function getFilePath(urlPath) {
  const cleanPath = urlPath.split('?')[0].split('#')[0];
  let target = path.join(DIST_DIR, cleanPath);

  // 1. exact match & file
  if (fs.existsSync(target) && fs.statSync(target).isFile()) {
    return target;
  }

  // 2. dir -> index.html
  if (fs.existsSync(target) && fs.statSync(target).isDirectory()) {
    const idx = path.join(target, 'index.html');
    if (fs.existsSync(idx)) return idx;
  }

  // 3. clean url -> .html
  const withHtml = `${target}.html`;
  if (fs.existsSync(withHtml) && fs.statSync(withHtml).isFile()) {
    return withHtml;
  }

  // 4. clean url -> dir/index.html
  const asDirIdx = path.join(target, 'index.html');
  if (fs.existsSync(asDirIdx) && fs.statSync(asDirIdx).isFile()) {
    return asDirIdx;
  }

  return null;
}

const server = http.createServer((req, res) => {
  const filePath = getFilePath(req.url);

  if (!filePath) {
    const notFoundPage = path.join(DIST_DIR, '404.html');
    if (fs.existsSync(notFoundPage)) {
      res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
      fs.createReadStream(notFoundPage).pipe(res);
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('404 Not Found');
    }
    return;
  }

  const ext = path.extname(filePath).toLowerCase();
  const mime = MIME_TYPES[ext] || 'application/octet-stream';

  res.writeHead(200, { 'Content-Type': mime });
  fs.createReadStream(filePath).pipe(res);
});

server.listen(PORT, () => {
  console.log(`[Dev Server] Running at http://localhost:${PORT}`);
  console.log(`[Dev Server] Serving directory: ${DIST_DIR}`);
});
