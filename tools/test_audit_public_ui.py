import unittest
from audit_public_ui import FORBIDDEN, audit

class UiCopyAuditTest(unittest.TestCase):
    def test_rejects_development_copy(self):
        self.assertTrue(FORBIDDEN.search("debug"))
        self.assertTrue(FORBIDDEN.search("Nije implementirano"))
        self.assertTrue(FORBIDDEN.search("Koncept dizajna"))
        self.assertFalse(FORBIDDEN.search("Videozapis"))
        self.assertFalse(FORBIDDEN.search("Fotografiranje"))

    def test_app_source_contains_no_developer_placeholders(self):
        self.assertEqual([], audit())

if __name__ == "__main__":
    unittest.main()
