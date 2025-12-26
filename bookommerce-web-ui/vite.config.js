import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: 'app.bookommerce.com',
    port: 8080,
    open: true,
    https: {
      pfx: fs.readFileSync(path.resolve(__dirname, 'bookommerce-ssl.p12')),
      passphrase: '123456'
    }
  }
})
