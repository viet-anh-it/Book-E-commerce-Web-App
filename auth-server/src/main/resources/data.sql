-- 1. Chèn dữ liệu vào bảng permissions
-- Sử dụng WHERE NOT EXISTS để tránh lỗi nếu chạy lại file nhiều lần
INSERT INTO permissions (operation, resource)
SELECT 'CREATE', 'BOOK' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE operation = 'CREATE' AND resource = 'BOOK');
INSERT INTO permissions (operation, resource)
SELECT 'READ', 'BOOK' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE operation = 'READ' AND resource = 'BOOK');
INSERT INTO permissions (operation, resource)
SELECT 'UPDATE', 'BOOK' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE operation = 'UPDATE' AND resource = 'BOOK');
INSERT INTO permissions (operation, resource)
SELECT 'DELETE', 'BOOK' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE operation = 'DELETE' AND resource = 'BOOK');

-- 2. Chèn dữ liệu vào bảng roles
INSERT INTO roles (name)
SELECT 'ROLE_PRODUCT_MANAGER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_PRODUCT_MANAGER');
INSERT INTO roles (name)
SELECT 'ROLE_CUSTOMER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_CUSTOMER');

-- 3. Gán quyền cho role ADMIN (Toàn quyền: CREATE, READ, UPDATE, DELETE)
-- Xóa các liên kết cũ để tránh duplicate nếu cần, hoặc dùng WHERE NOT EXISTS
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.name = 'ROLE_PRODUCT_MANAGER' 
AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- 4. Gán quyền cho role USER (Chỉ quyền READ)
INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.name = 'ROLE_CUSTOMER' AND p.operation = 'READ' AND p.resource = 'BOOK'
AND NOT EXISTS (SELECT 1 FROM roles_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);

-- 5. Chèn User mới
-- Mật khẩu '123456' đã được hash bằng BCrypt (độ dài 60 ký tự)
-- Hash mẫu: $2a$10$cYLM.qoXpeAzcZhJ3oXRLu9Slkb61LHyWW5qJ4QKvHEMhaxZ5qCPi
INSERT INTO users (email, password_hash, is_activated)
SELECT 'prodman@bookommerce.com', '$2a$10$cYLM.qoXpeAzcZhJ3oXRLu9Slkb61LHyWW5qJ4QKvHEMhaxZ5qCPi', 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'prodman@bookommerce.com');

-- 6. Gán Role ROLE_PRODUCT_MANAGER cho User vừa tạo
-- Giả sử bảng trung gian mặc định của Hibernate cho User và Role là 'users_roles'
-- (Nếu bạn đã đổi tên bằng @JoinTable, hãy thay 'users_roles' bằng tên đó)
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'prodman@bookommerce.com' AND r.name = 'ROLE_PRODUCT_MANAGER'
AND NOT EXISTS (
    SELECT 1 FROM users_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);