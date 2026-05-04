USE upes_lost_found;

INSERT INTO users(full_name, university_id, email, phone, password_hash, role, active) VALUES
('Campus Admin', 'UPES-ADMIN-001', 'admin@upes.ac.in', '9999999999',
 '65536:BXS94up0pDhurzF3FtnziQ==:qdhbJljowxqcoIkjoYUO0bPQ2XLm+yxFwY49J8lGPZI=', 'ADMIN', TRUE),
('Aarav Sharma', 'SAP500112233', 'student@upes.ac.in', '9876543210',
 '65536:yAo2cWtvwWVj4oLxBnA4mQ==:Ck9aa0ZpehILhQKrgWDfMPdLmcPzxvpfTKNZF8IQESU=', 'STUDENT', TRUE),
('Meera Rawat', 'SAP500112244', 'meera@upes.ac.in', '9876543211',
 '65536:yAo2cWtvwWVj4oLxBnA4mQ==:Ck9aa0ZpehILhQKrgWDfMPdLmcPzxvpfTKNZF8IQESU=', 'STUDENT', TRUE),
('Rohan Bisht', 'SAP500112255', 'rohan@upes.ac.in', '9876543212',
 '65536:yAo2cWtvwWVj4oLxBnA4mQ==:Ck9aa0ZpehILhQKrgWDfMPdLmcPzxvpfTKNZF8IQESU=', 'STUDENT', TRUE);

INSERT INTO item_reports(
    user_id, report_type, item_name, category, description, location, item_date,
    image_path, contact_name, contact_phone, storage_location, status
) VALUES
(2, 'LOST', 'Black Dell laptop', 'Electronics',
 'Black Dell Inspiron laptop with a UPES coding club sticker on the lid. Last used before DBMS lab.',
 'Computer Lab', '2026-04-28', '', 'Aarav Sharma', '9876543210', NULL, 'MATCHED'),
(3, 'FOUND', 'Dell laptop with coding sticker', 'Electronics',
 'Found a black Dell laptop with coding club sticker near lab desk row 2.',
 'Computer Lab', '2026-04-28', '', 'Meera Rawat', '9876543211', 'Security Desk, Admin Block', 'APPROVED'),
(4, 'FOUND', 'Blue umbrella', 'Other',
 'Blue folding umbrella found near cafeteria exit after lunch hours.',
 'Cafeteria', '2026-04-29', '', 'Rohan Bisht', '9876543212', 'Cafeteria counter', 'APPROVED'),
(2, 'LOST', 'Brown wallet', 'Wallet',
 'Brown leather wallet with student ID and metro card. Might be near parking or admin block.',
 'Parking Area', '2026-04-30', '', 'Aarav Sharma', '9876543210', NULL, 'PENDING'),
(3, 'LOST', 'Engineering Mathematics book', 'Books',
 'Engineering Mathematics book by B.S. Grewal with Meera written on first page.',
 'Library', '2026-05-01', '', 'Meera Rawat', '9876543211', NULL, 'APPROVED');

INSERT INTO claim_requests(item_id, claimant_id, message, status, admin_note) VALUES
(2, 2, 'It has a UPES coding club sticker and my VS Code project folder is named ds-mini-project.', 'PENDING', NULL);

INSERT INTO notifications(user_id, title, message) VALUES
(2, 'Possible match found', 'A found Dell laptop looks similar to your lost laptop report.'),
(3, 'Report approved', 'Your found laptop report is visible to students now.');
