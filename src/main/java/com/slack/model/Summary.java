package com.slack.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by sneha.dontireddy on 8/7/18.
 */
@Data
@Setter
public class Summary {
    int totalDeployed;
    int totalStaged;
    int percentOfStagedToDeployed;
    List<String> Contributors;
}
