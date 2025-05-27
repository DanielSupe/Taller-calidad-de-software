Feature: Delete device
  Scenario: Attempt to delete a loaned device
    Given a device is registered with name Projector, type Multimedia, and location Room101
    And the device is currently loaned to user Alice
    When the user attempts to delete the device
    Then the device should not be deleted
    And an error message Failed to delete device should be displayed
