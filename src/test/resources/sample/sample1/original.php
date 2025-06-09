<?php

class Original
{
    // Public properties (various types)
    public $publicString = "Hello World";
    public $publicInt = 42;
    public $publicFloat = 3.14159;
    public $publicBool = true;
    public $publicNull = null;
    public $publicArray = ["apple", "banana", "cherry"];
    
    // Protected properties (various types)
    protected $protectedString = "Protected Data";
    protected $protectedInt = 100;
    protected $protectedFloat = 2.71828;
    protected $protectedBool = false;
    protected $protectedArray = [
        "name" => "John Doe",
        "age" => 30,
        "skills" => ["PHP", "JavaScript", "Python"]
    ];
    
    // Private properties (various types)
    private $privateString = "Private Secret";
    private $privateInt = 999;
    private $privateFloat = 1.41421;
    private $privateBool = true;
    private $privateArray = [
        "config" => [
            "debug" => true,
            "version" => "1.0.0",
            "database" => [
                "host" => "localhost",
                "port" => 3306
            ]
        ]
    ];
    
    // Nested object
    public $nestedObject = null;
    
    public function __construct()
    {
        // Create a nested object instance
        $this->nestedObject = new NestedData();
    }
    
    public function getPublicData()
    {
        return [
            "string" => $this->publicString,
            "int" => $this->publicInt,
            "float" => $this->publicFloat,
            "bool" => $this->publicBool,
            "null" => $this->publicNull,
            "array" => $this->publicArray
        ];
    }
    
    protected function getProtectedData()
    {
        return [
            "string" => $this->protectedString,
            "int" => $this->protectedInt,
            "float" => $this->protectedFloat,
            "bool" => $this->protectedBool,
            "array" => $this->protectedArray
        ];
    }
    
    private function getPrivateData()
    {
        return [
            "string" => $this->privateString,
            "int" => $this->privateInt,
            "float" => $this->privateFloat,
            "bool" => $this->privateBool,
            "array" => $this->privateArray
        ];
    }
}

class NestedData
{
    // Public properties
    public $dataType = "nested";
    public $level = 1;
    public $items = [];
    
    // Protected properties
    protected $metadata = [
        "created_at" => "2024-01-01T00:00:00Z",
        "updated_at" => "2024-06-10T12:00:00Z",
        "version" => "1.2.0"
    ];
    
    // Private properties
    private $internalConfig = [
        "cache_enabled" => true,
        "timeout" => 30,
        "retry_count" => 3
    ];
    
    public function __construct()
    {
        $this->items = [
            "item1" => [
                "id" => 101,
                "name" => "First Item",
                "active" => true
            ],
            "item2" => [
                "id" => 102,
                "name" => "Second Item", 
                "active" => false
            ]
        ];
    }
}
