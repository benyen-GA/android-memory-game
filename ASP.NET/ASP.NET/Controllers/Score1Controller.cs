using ASP.NET.DTO;
using Microsoft.AspNetCore.Mvc;

namespace ASP.NET.Controllers;

[Route("api/[controller]")]
public class Score1Controller : Controller
{
    private readonly ScoreService _service;
    private readonly ILogger<Score1Controller> _logger;

    public Score1Controller(ScoreService service, ILogger<Score1Controller> logger)
    {
        _service = service;
        _logger = logger;
    }
    
    //for playFragment add score
    [HttpPost("add")]
    public IActionResult AddScore([FromBody] ScoreRequest scoreRequest)
    {
        try
        {
            _service.AddScore(scoreRequest);
            return Ok();
        }
        catch (Exception exception)
        {
            _logger.LogError("Exception occurred: {Message}", exception.Message);
            return StatusCode(500, "Unexpected error occur.");        }
    }
}