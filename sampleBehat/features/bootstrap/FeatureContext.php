<?php

use Behat\Behat\Context\ClosuredContextInterface,
    Behat\Behat\Context\TranslatedContextInterface,
    Behat\Behat\Context\BehatContext,
    Behat\Behat\Exception\PendingException;
use Behat\Gherkin\Node\PyStringNode,
    Behat\Gherkin\Node\TableNode;

use Behat\Mink\Mink,
Behat\Mink\Session,
Behat\Mink\Driver\Selenium2Driver;

use Selenium\Client as SeleniumClient;
use Behat\MinkExtension\Context\MinkContext;


//
// Require 3rd-party libraries here:
//
//   require_once 'PHPUnit/Autoload.php';
//   require_once 'PHPUnit/Framework/Assert/Functions.php';
//

/**
 * Features context.
 */
class FeatureContext extends MinkContext
{
	
	
    /**
     * @Given /^i wait for (\d+) seconds$/
     */
    public function iWaitForSeconds($waitSeconds)
    {
    	sleep($waitSeconds);
    }
    

}
