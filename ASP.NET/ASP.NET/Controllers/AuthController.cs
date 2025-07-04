using ASP.NET.Models;
using Microsoft.AspNetCore.Mvc;

namespace ASP.NET.Controllers;


[Route("api/[controller]")]
public class AuthController : Controller
{
    private AuthService _service;
    private readonly ILogger<AuthController> _logger;
    

    public AuthController(AuthService service, ILogger<AuthController>  logger)
    {
        _service = service;
        _logger = logger;
    }
    
    [HttpPost("login")]
    public IActionResult Login([FromBody] LoginRequest loginRequest)
    {
        try
        {
            User user = _service.Authenticate(loginRequest.username, loginRequest.password);
            if (user == null)
            {
                return Unauthorized("Invalid credentials");
            }

            return Ok(new { user.Id, user.Username, user.IsPaidUser });

        }
        catch (Exception exception)
        {
            _logger.LogError("Exception occurred: {Message}", exception.Message);
            return StatusCode(500, "Unexpected error occur.");
        }
        
    }
    
}