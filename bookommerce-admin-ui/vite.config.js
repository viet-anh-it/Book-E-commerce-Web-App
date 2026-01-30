import react from '@vitejs/plugin-react'
import fs from 'fs'
import path from 'path'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: 'admin.bookommerce.com',
    port: 7979,
    open: false,
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'admin-ui-private.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'admin-ui-cert.crt')),
    }
  }
})
