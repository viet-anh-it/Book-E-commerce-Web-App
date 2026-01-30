import react from '@vitejs/plugin-react'
import fs from 'fs'
import path from 'path'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: 'app.bookommerce.com',
    port: 8080,
    open: false,
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'web-ui-private.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'web-ui-cert.crt')),
    }
  }
})
