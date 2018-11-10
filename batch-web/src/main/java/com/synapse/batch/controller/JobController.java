package com.synapse.batch.controller;

import com.synapse.batch.model.Job;
import com.synapse.batch.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by ivaneye on 17-3-8.
 */
@Controller
@RequestMapping("/batch/job/v1")
public class JobController {

    @Autowired
    private JobService jobService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Job> jobs = jobService.list();
        model.addAttribute("jobs", jobs);
        return "job/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String _new(Model model)  {
        return "job/new";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(Model model, Job job) {
        jobService.add(job);
        return "redirect:/batch/job/v1/list";
    }

    @RequestMapping(value = "/delete/{recId}", method = RequestMethod.DELETE)
    @ResponseBody
    public String delete(Model model, @PathVariable long recId) {
        try {
            jobService.deleteById(recId);
            return "true";
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }

    @RequestMapping(value = "/edit/{recId}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable long recId) {
        Job job = jobService.queryById(recId);
        model.addAttribute("job", job);
        return "job/edit";
    }

    @RequestMapping(value = "/view/{recId}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable long recId) {
        Job job = jobService.queryById(recId);
        model.addAttribute("job", job);
        return "job/view";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public String update(Model model, Job job) {
        jobService.update(job);
        return "redirect:/batch/job/v1/list";
    }

    @RequestMapping(value = "/start/{recId}",method = RequestMethod.POST)
    @ResponseBody
    public String start(Model model, @PathVariable long recId) {
        try {
            jobService.start(recId);
            return "true";
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }

    @RequestMapping(value = "/stop/{recId}",method = RequestMethod.POST)
    @ResponseBody
    public String stop(Model model, @PathVariable long recId) {
        try {
            jobService.stop(recId);
            return "true";
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }
}
