import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: 'admin.bookommerce.com',
    port: 7979,
    open: false,
    https: {
      pfx: fs.readFileSync(path.resolve(__dirname, 'bookommerce-ssl.p12')),
      passphrase: '123456'
    }
  }
})
